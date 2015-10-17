/**
 * Created by boris on 10/17/15.
 */
$('body').on('click', '#cancelReservation[data-role="cancel"]', function (e) {
    e.preventDefault();
    var conf = bootbox.confirm({
        className: "delete-modal-font",
        message: "Are you sure you want to cancel this reservation?",
        callback: function (result) {
            if (result != false) {
                $.ajax({
                    url: $('#cancelReservation').attr("href"),
                    method: "post",
                    data: "value=" + $('#cancelReservation').attr("value")
                }).success(function (response) {
                    location.reload();
                });
            }
        }});
});
