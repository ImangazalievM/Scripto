function loadUserData() {
   PreferencesInterface.getUserData(function(userJson) {
        var user = JSON.parse(userJson);
        document.getElementById('name_field').value = user.name;
        document.getElementById('surname_field').value = user.surname;
        document.getElementById('age_field').value = user.age;
        document.getElementById('height_field').value = user.height;
        document.getElementById('married_checkbox').checked = user.married;
    });
}

function saveUserData() {
    var user = getUserData();
    PreferencesInterface.saveUserData(user);
    AndroidInterface.showToastMessage("Saved");
}

function getUserData() {
    var user = {};
    user['name'] = document.getElementById('name_field').value;
    user['surname'] = document.getElementById('surname_field').value;
    user['age'] = document.getElementById('age_field').value;
    user['height'] = document.getElementById('height_field').value;
    user['married'] = document.getElementById('married_checkbox').checked;

    return JSON.stringify(user);
}

//после окончания загрузки документа, грузим данные пользователя
document.addEventListener('DOMContentLoaded', function() {
    loadUserData();
}, false);