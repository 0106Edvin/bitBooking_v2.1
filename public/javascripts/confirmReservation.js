/**
 * Created by Alen Bumbulovic on 10/27/2015.
 */

$('body').on('click', '#payPal', function (e) {
    e.preventDefault();
    $toDelete = $(this);
    swal({
        title: 'Are you sure you want to book this room?',
        text: 'Please confirm your reservation.',
        type: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'No, cancel!',
        confirmButtonClass: 'confirm-class',
        cancelButtonClass: 'cancel-class',
        closeOnConfirm: false,
        closeOnCancel: false
    }, function (isConfirm) {
        if (isConfirm) {
            $.ajax({
                url: $toDelete.attr("href"),
                method: "post",
                data: "value=" + $toDelete.attr("roomId")

            }).success(function (response) {
                $toDelete.parents($toDelete.attr("data-delete-parent")).remove();
                swal({
                    title: 'Comment deleted!',
                    text: 'You successfully deleted comment.',
                    type: 'success',
                    timer: 1000
                });
            });
        } else {
            swal({
                title: 'Canceled!',
                text: 'Delete canceled.',
                type: 'error',
                timer: 1000
            });
        }
    });
});