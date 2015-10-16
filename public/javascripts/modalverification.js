/**
 * Created by stvorenje on 10/14/15.
 */

function validatePrice(){
    var price = document.getElementById('modal-price').value;
    var save = document.getElementById('modal-button-blue');
    var s = document.getElementById('price-missing');

    save.disabled = true;

    if(price == ""){
        s.innerHTML = 'Please, set room price value and then save.';
    }

    if(price != ""){
        save.disabled = false;
    }

}

