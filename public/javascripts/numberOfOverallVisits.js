/**
 * Created by boris.tomic on 13/10/15.
 */
$(document).ready(setInterval(function(){
    $.ajax({
        type: "get",
        url: "overallvisits"
    }).success(function(response) {
        $('#overall').html(response);
    })
}, 3500));