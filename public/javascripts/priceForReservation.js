/**
 * Created by boris on 10/8/15.
 */
var sendData = function(roomId){
    $.ajax({
        type: 'POST',
        url: '/priceselection',
        data: 'room=' + roomId + '&date=' + $('#date').val() + '&date2=' + $('#date2').val()
    }).success(function(response) {
        $('#price-for-period').html("Price for selected period: " + response + " $");
        return response;

    })
};
