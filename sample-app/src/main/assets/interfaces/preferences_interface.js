var PreferencesInterface = {

    saveUserData: function(user) {
        Scripto.call('Preferences', arguments);
    },

    getUserData: function(callback) {
        Scripto.callWithCallback('Preferences', arguments);
    }

}