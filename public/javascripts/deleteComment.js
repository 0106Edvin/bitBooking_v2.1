/**
 * Created by boris on 10/15/15.
 */
$('body').on('click', '#commDel[data-role="delete"]', function (e) {
    e.preventDefault();
    $toDelete = $(this);
    swal({
        title: 'Are you sure you want to delete this comment?',
        text: 'You are about to delete from database.',
        type: 'warning',
        showCancelButton: true,
        showConfirmButton: true,
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
                    title: 'Comment deleted!',
                    text: 'You successfully deleted comment.',
                    type: 'success',
                    timer: 1000
                });
            });
        }
    });
});
