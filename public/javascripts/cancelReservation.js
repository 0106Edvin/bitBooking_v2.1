/**
 * Created by boris on 10/17/15.
 */
$('body').on('click', '#cancelReservation[data-role="cancel"]', function (e) {
    e.preventDefault();
    $toCancel = $(this);
    swal({
        title: 'Are you sure you want to cancel reservation?',
        text: 'You are about to cancel reservation.',
        type: 'warning',
        showCancelButton: true,
        chowConfirmButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Yes, cancel it!',
        cancelButtonText: 'No, keep reservation!',
        confirmButtonClass: 'confirm-class',
        cancelButtonClass: 'cancel-class',
        showLoaderOnConfirm: true,
        closeOnConfirm: false,
        closeOnCancel: true
    }, function (isConfirm) {
        swal.disableButtons();
        if (isConfirm) {
            $.ajax({
                url: $toCancel.attr("href"),
                method: "post",
                data: "value=" + $toCancel.attr("res-id")
            }).success(function (response) {
                location.reload();
                swal({
                    title: 'Reservation canceled!',
                    text: 'You will be fully refunded.',
                    type: 'success',
                    timer: 1000
                });
            });
        }
    });
});

