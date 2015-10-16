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
function changeSeller(){
    var result = confirm("Do you want to change seller for this hotel?");
    if(result){
        document.getElementById('success').innerHTML = "Seller updated";

        return true;
    }else{
        document.getElementById('error').innerHTML = "Seller is not updated";
        return false;


    }
}
