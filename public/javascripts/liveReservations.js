/**
 * Created by ajla on 12/11/15.
 */
$(document).ready(setInterval(function(){
    $.ajax({
        type: "get",
        url: "/"
    }).success(function(response) {
        $("#liveRecentReservations").load(location.href + " #liveRecentReservations");
    })
}, 2000));


