function AndroidInterface() {}

AndroidInterface.showToastMessage = function(text) {
  Scripto.call('Android', arguments);
};