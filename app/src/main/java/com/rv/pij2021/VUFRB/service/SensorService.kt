package com.rv.pij2021.VUFRB.service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.rv.pij2021.VUFRB.R
import com.rv.pij2021.VUFRB.ml.Regressor
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

/*
Gerencia os dados de localização e de direção do celular (para onde a câmera está apontnando) .
 */
class SensorService(private val contextActivity: Activity) : SensorEventListener {
    private var locationManager: LocationManager? = null
    private var locationListenerGPS: LocationListener? = null
    private var locationListenerNet: LocationListener? = null
    private val sensorManager: SensorManager

    private lateinit var model : Regressor
    private val geomagneticRotationReading = FloatArray(3)
    private val acceReading = FloatArray(3)
    private val geomagMax = FloatArray(3)
    private val acceMax = FloatArray(3)

    @JvmField
    var latitude = -12.697140299262665 // y
    @JvmField
    var longitude = -39.193479862331124 // x

    fun positionNetwork() {

            Log.i("RAUFRB", "PositionNetwork: ("+latitude+","+latitude);
            locationManager = contextActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(contextActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationPermission
            }
            var gps_enabled = false
            var network_enabled = false
            try {
                gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (ex: Exception) {
            }
            try {
                network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } catch (ex: Exception) {
            }
            if (!gps_enabled && !network_enabled) {
                return
            }

            // percebi que raramente se consegue obter dados GPS
            locationListenerGPS = LocationListener { location ->
                latitude = location.latitude
                longitude = location.longitude
                Log.i("RAUFRB", "GPS-> Lat: $latitude, Lon: $longitude")
            }
            locationListenerNet = LocationListener { location ->
                latitude = location.latitude
                longitude = location.longitude
                Log.i("RAUFRB", "NET-> Lat: $latitude, Lon: $longitude")
            }
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0f, locationListenerNet!!)
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0f, locationListenerGPS!!)
        }
    private val locationPermission: Unit
        private get() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(contextActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                val builder = AlertDialog.Builder(contextActivity)
                builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_sim) { dialog, which ->
                    dialog.cancel()
                    ActivityCompat.requestPermissions(contextActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                }.show()
            } else {
                val builder = AlertDialog.Builder(contextActivity)
                builder.setMessage(R.string.GPS_permissions).setCancelable(false).setPositiveButton(R.string.btn_olhar_perm) { dialog, which ->
                    dialog.cancel()
                    contextActivity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + contextActivity.packageName)))
                }.setNegativeButton(R.string.btn_fechar) { dialog, which -> dialog.cancel() }.show()
            }
        }

    fun onPause() {
        if (locationManager != null) {
            if (locationListenerGPS != null) {
                locationManager!!.removeUpdates(locationListenerGPS!!)
            }
            if (locationListenerNet != null) {
                locationManager!!.removeUpdates(locationListenerNet!!)
            }
        }
        sensorManager.unregisterListener(this)

        model.close()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    fun onResume() {
        // cadastra o sensor TYPE_GEOMAGNETIC_ROTATION_VECTOR para receber atualizações dele.
        setSesorListener(sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR))
        setSesorListener(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))

        positionNetwork()

        model = Regressor.newInstance(contextActivity)
    }

    // define o Listener dos sensores.
    private fun setSesorListener(sensor: Sensor?) {
        if (sensor != null) {
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    // método que recebe as mudanças dos sensores
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) { //melhor: TYPE_GEOMAGNETIC_ROTATION_VECTOR
            System.arraycopy(event.values, 0, geomagneticRotationReading,
                    0, 3)
        }else{
            System.arraycopy(event.values, 0, acceReading,
                    0, 3)
        }

        updateMax()
    }

    // para mapear o valor de x para entre -1 e 1
    // obtem os valores máximos obtidos pelos sensores
    fun updateMax(){
        for (i in 0..2) {
            if (Math.abs(geomagneticRotationReading[i]) > geomagMax[i]) {
                geomagMax[i] = geomagneticRotationReading[i]
            }
            if (Math.abs(acceReading[i]) > acceMax[i]) {
                acceMax[i] = acceReading[i]
            }
        }
    }

    // retorna os valores dos sensores em intervalos de -1 a 1. É um vetor de 6 posições.
    // as 3 primeiras posições (0-2) são referentes ao sensor geomagnetico ou bússula
    // as 3 ultimas posições são referentes aos dados do sensor acelerômetro
    fun valuesSensor(): FloatArray {

        // mapeia o valor de x para entre -1 e 1
        val values = FloatArray(6)
        values[0] = geomagneticRotationReading[0] / geomagMax[0] * 10f // limita para entre -1 e 1 e sobe 2 casas decimais
        values[0] = Math.abs(values[0].toInt() / 10f) // retira 2 casas decimais e deixa sempre positivo.

        for (i in 1..5) {
            if (i < 3) {
                values[i] = geomagneticRotationReading[i] / geomagMax[i] * 10f // limita para entre -1 e 1 e sobe 2 casas decimais.
                values[i] = values[i].toInt() / 10f // retira 2 casas decimais e mantém o sinal.
            } else { // o mesmo para acc
                values[i] = acceReading[i - 3] / acceMax[i - 3] * 100.0f
                values[i] = values[i].toInt() / 100f
            }
        }
        return values
    }

    // theta é positivo para sentido anti-horário. Theta é um ângulo entre o vetor normal à camera do celular e o eixo y
    // o eixo y é o eixo sul-norte
    // seu intervalo obtido na inferencia é de -PI a PI, por limitações do regressor.
    // o retorno de getTheta é de 0 a 2PI
    // note que theta está no plano da terra.
    fun getTheta(): Double {

        // Creates inputs for reference.
        val vetor = TensorBuffer.createFixedSize(intArrayOf(1, 6), DataType.FLOAT32)
        //vetor.loadBuffer(byteBuffer);
        vetor.loadArray(valuesSensor())
        // Runs model inference and gets result.
        // Releases model resources if no longer used.
        val theta = model.process(vetor).getProbabilityAsTensorBuffer().getFloatValue(0)

        if (theta > 0){
            return theta + .0
        }else{
            return 2*Math.PI + theta
        }
    }

    init {
        sensorManager = contextActivity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    // R é um número real que é a distância entre o celular e o bloco que está a (lon, lat) == (x,y)
    public fun getR(lon : Float, lat: Float): Double{
        return Math.sqrt(Math.pow(lon-longitude, 2.0)+Math.pow(lat-latitude, 2.0))
    }


    // phi é o angulo entre o vetor normal à câmera do celular e o eixo normal à terra.
    // Esse eixo normal é paralelo ao vetor direção da gravidade.
    // phi é equivalente ao angulo entre o eixo z e o raio, de coordenadas esféricas.
    // mapeia valores de -accMax a accMax para 0 a PI
    public fun getPhi(): Double {
        val a = Math.PI/(2*acceMax[2])
        return a*(valuesSensor()[5]-acceMax[2])
    }
}


fun getR(lon : Double, lat: Double, objLon : Float, objLat : Float): Double {
    return Math.sqrt(Math.pow(objLon-lon, 2.0)+Math.pow(objLat-lat, 2.0))
}