import tensorflow as tf

def regressor():

    inputs = tf.keras.Input(shape=(6,1))

    extract = tf.keras.layers.Conv1D(36, 3, activation='relu')(inputs)
    extract = tf.keras.layers.BatchNormalization()(extract)

    one_d = tf.keras.layers.Flatten()(extract)
    one_d = tf.reshape(one_d, shape=(6,))
    one_d = tf.keras.layers.Concatenate()([inputs,one_d])

    dense = tf.keras.layers.Dense(6, activation='relu')(one_d)
    dense = tf.keras.layers.Dense(1)(dense)

    model = tf.keras.Model(inputs=inputs, outputs=dense)

    model.compile(
        optimizer=tf.optimizers.Adam(),
        loss='mse',
        metrics=['mae', 'mse']
    )

    return model
tf.nn.sigmoid
model = regressor().fit(batch_size=None,
          epochs=1,
          verbose='auto',)