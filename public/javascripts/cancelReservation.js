/**
 * Created by boris on 10/17/15.
 */
$('body').on('click', '#cancelReservation[data-role="cancel"]', function (e) {
    e.preventDefault();
    $toCancel = $(this);
    var conf = bootbox.confirm({
        className: "delete-modal-font",
        message: "Are you sure you want to cancel this reservation?",
        callback: function (result) {
            if (result != false) {
                $.ajax({
                    url: $toCancel.attr("href"),
                    method: "post",
                    data: "value=" + $toCancel.attr("res-id")
                }).success(function (response) {
                    location.reload();
                });
            }
        }});
});
