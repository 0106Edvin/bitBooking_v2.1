/**
 * Created by boris on 10/7/15.
 */
$(document).ready(setInterval(function(){
    $.ajax({
        type: "get",
        url: "approfednotif"
    }).success(function(response) {
        if("0"!=response){
            $("#changeVisibility").css("display","");
            $('#notify').html(response);
        }
    })
}, 2000));
