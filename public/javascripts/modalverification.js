/**
 * Created by stvorenje on 10/14/15.
 */

function validatePrice(){
    var price = document.getElementById("modal-price").value;
    var save = document.getElementById("modal-button-blue");

    save.isDisabled = true;
    var input = document.getElementById("modal-price");

    input.background = "red";
    input.borderColor = "red";

}