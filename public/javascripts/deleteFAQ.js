/**
 * Created by Alen Bumbulovic on 10/26/2015.
 */

$('body').on('click', 'a[data-role="delete"]', function (e) {
    e.preventDefault();
    $toDelete = $(this);
    swal({
        title: 'Are you sure you want to delete?',
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
                $toDelete.parents($toDelete.attr("data-delete-parent")).remove();
                swal({
                    title: 'Deleted!',
                    text: 'You successfully deleted.',
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