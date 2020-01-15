// ./public/javascript.js

// Get the current username from the cookies
var user = cookie.get('user');

var socket = io();

// When the form is submitted
$('form').submit(function (e) {

  // Avoid submitting it through HTTP
  e.preventDefault();

  // Retrieve the message from the user
  var message = $(e.target).find('textarea').val();

  // Send the message to the server
  socket.emit('message', {
    user: cookie.get('user') || 'Anonymous',
    message: message
  });

  // Clear the input and focus it for a new message
  e.target.reset();
  $(e.target).find('input').focus();
});