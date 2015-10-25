/**
 * Created by boris on 10/25/15.
 */
$(document).ready(setInterval(function(){
    $.ajax({
        type: "get",
        url: "/messagenotif"
    }).success(function(response) {
        if("0"!=response){
            $('#msgs').html(response);
        }
    })
}, 2000));
