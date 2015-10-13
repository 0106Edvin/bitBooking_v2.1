/**
 * Created by boris.tomic on 13/10/15.
 */
$(document).ready(setInterval(function(){
    $.ajax({
        type: "get",
        url: "uniquevisits"
    }).success(function(response) {
        $('#unique').html(response);

    })
}, 3500));