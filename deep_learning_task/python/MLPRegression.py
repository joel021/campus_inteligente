from sklearn.neural_network import MLPRegressor
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression

regressor = MLPRegressor(hidden_layer_sizes = (10,2),
                         activation='logistic',
                         learning_rate_init=0.1,
                         learning_rate='adaptive',
                         batch_size=256,
                         tol = 0.00001,
                         verbose=True,
                         n_iter_no_change=100,
                         random_state=1,
                         max_iter=55000)

print(regressor.hidden_layer_sizes)
