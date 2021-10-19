import tensorflow as tf
from keras.datasets import mnist

# Parameters
learning_rate = 0.001
training_epochs = 15
batch_size = 100
display_step = 1
# Network Parameters
n_hidden_1 = 4 # 1st layer number of neurons
n_hidden_2 = 4 # 2nd layer number of neurons
n_input = 6 # MNIST data input (img shape: 28*28)
n_classes = 1 # MNIST total classes (0-9 digits)

# tf Graph input
X = tf.compat.v1.placeholder("float", [None, n_input])
Y = tf.compat.v1.placeholder("float", [None, n_classes])
# Store layers weight & bias
weights = {
    'h1': tf.Variable(tf.random_normal_initializer([n_input, n_hidden_1])),
    'out': tf.Variable(tf.random_normal_initializer([n_hidden_2, n_classes]))
}
biases = {
    'b1': tf.Variable(tf.random_normal_initializer([n_hidden_1])),
    'out': tf.Variable(tf.random_normal_initializer([n_classes]))
}
def multilayer_perceptron(x):
    # Hidden fully connected layer with 256 neurons
    layer_1 = tf.add(tf.matmul(x, weights['h1']), biases['b1'])
    # Output fully connected layer with a neuron for each class
    out_layer = tf.matmul(layer_1, weights['out']) + biases['out']
    return tf.nn.relu(out_layer)
# Construct model

logits = multilayer_perceptron(X)

# Define loss and optimizer
loss_op = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=logits, labels=Y))
optimizer = tf.optimizers.AdamOptimizer(learning_rate=learning_rate)
train_op = optimizer.minimize(loss_op)

# Initializing the variables
init = tf.compat.v1.global_variables_initializer()

with tf.compat.v1.Session as sess:
    sess.run(init)
    # Training cycle
    for epoch in range(training_epochs):
        avg_cost = 0.
        total_batch = int(mnist.train.num_examples/batch_size)
        # Loop over all batches
        for i in range(total_batch):
            batch_x, batch_y = mnist.train.next_batch(batch_size)
            # Run optimization op (backprop) and cost op (to get loss
            value)
            _, c = sess.run([train_op, loss_op], feed_dict={X: batch_x,Y:
            batch_y})
            # Compute average loss
            avg_cost += c / total_batch

# Display logs per epoch step
if epoch % display_step == 0:
print("Epoch:", '%04d' % (epoch+1), "cost=
{:.9f}".format(avg_cost))
print("Optimization Finished!")

# Test model
pred = tf.nn.softmax(logits) # Apply softmax to logits
correct_prediction = tf.equal(tf.argmax(pred, 1), tf.argmax(Y,
1))

# Calculate accuracy
accuracy = tf.reduce_mean(tf.cast(correct_prediction, "float"))
print("Accuracy:", accuracy.eval({X: mnist.test.images, Y:
mnist.test.labels}))
