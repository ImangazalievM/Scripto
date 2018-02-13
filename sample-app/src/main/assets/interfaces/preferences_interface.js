function PreferencesInterface() {}

PreferencesInterface.saveUserData = function(user) {
  Scripto.call('Preferences', arguments);
};

PreferencesInterface.getUserData = function(callback) {
  Scripto.callWithCallback('Preferences', arguments);
};