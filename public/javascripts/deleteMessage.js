/**
 * Created by boris on 10/25/15.
 */
$('body').on('click', '#delMessage[data-role="delete"]', function (e) {
    e.preventDefault();
    $toDelete = $(this);
    swal({
        title: 'Are you sure you want to delete message?',
        text: 'You are about to delete from database.',
        type: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'No, cancel!',
        confirmButtonClass: 'confirm-class',
        cancelButtonClass: 'cancel-class',
        showLoaderOnConfirm: true,
        closeOnConfirm: false,
        closeOnCancel: true
    }, function (isConfirm) {
        swal.disableButtons();
        if (isConfirm) {
            $.ajax({
                url: $toDelete.attr("href"),
                method: "delete"
            }).success(function (response) {
                $toDelete.parents($toDelete.attr("data-delete-parent")).remove();
                swal({
                    title: 'Message deleted!',
                    text: 'You successfully deleted message.',
                    type: 'success',
                    timer: 1000
                });
            }).error(function (response) {
                swal({
                    title: 'Could not delete message!',
                    text: 'Contact our staff for more information.',
                    type: 'error',
                    timer: 2500
                });
            });
        }
    });
});

