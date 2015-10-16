/**
 * Created by boris on 10/15/15.
 */
$('body').on('click', '#commDel[data-role="delete"]', function (e) {
    e.preventDefault();
    $toDelete = $(this);
    var conf = bootbox.confirm({
        className: "delete-modal-font",
        message: "Are you sure you want to delete this comment?",
        callback: function (result) {
        if (result != false) {
            $.ajax({
                url: $toDelete.attr("href"),
                method: "delete"
            }).success(function (response) {
                $toDelete.parents($toDelete.attr("data-delete-parent")).remove();
            });
        }
    }});
});
