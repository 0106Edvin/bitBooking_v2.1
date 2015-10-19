/**
 * Created by boris on 10/15/15.
 */
$('body').on('click', '#restDel[data-role="delete"]', function (e) {
    e.preventDefault();
    $toDelete = $(this);
    swal({
        title: 'Are you sure you want to delete restaurant?',
        text: 'You are about to delete from database.',
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
                method: "delete"
            }).success(function (response) {
                window.location.href = '/seller/sellerPanel/' + response;
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