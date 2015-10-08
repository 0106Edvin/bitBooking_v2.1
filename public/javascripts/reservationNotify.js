/**
 * Created by boris on 10/7/15.
 */
$(document).ready(setInterval(function(){
    $.ajax({
        type: "get",
        url: "approfednotif"
    }).success(function(response) {
        if("0"!=response){
            $('#notify').html(response + " New");
        }
    })
}, 2000));
