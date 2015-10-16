/**
 * Created by boris on 10/15/15.
 */
$('body').on('click', '#restDel[data-role="delete"]', function (e) {
    e.preventDefault();
    $toDelete = $(this);
    var conf = bootbox.confirm({
        className: "delete-modal-font",
        message: "Are you sure you want to delete restaurant?",
        callback: function (result){
        if (result != false) {
            $.ajax({
                url: $toDelete.attr("href"),
                method: "delete"
            }).success(function (response) {
                console.log(response);
                window.location.href = '/seller/sellerPanel/' + response;
            });
        }
    }});
});