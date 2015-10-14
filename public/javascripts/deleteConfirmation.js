/**
 * Created by User on 9/13/2015.
 */
function deleteConfirmation() {
    var result = confirm("Do you want to delete?");
    if (result) {
        return true;
    } else {
        return false;
    }
}



