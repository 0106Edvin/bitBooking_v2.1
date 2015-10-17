/**
 * Created by boris on 10/17/15.
 */
$('body').on('click', '#newsletter', function (e) {
    e.preventDefault();
    $subscribe = $(this);
    $email = $('#newsmail').val();

    $.ajax({
        url: $subscribe.attr("href"),
        method: "post",
        data: "email=" + $email
    }).success(function (response) {
        bootbox.alert({
            className: "delete-modal-font",
            message: "You successfully signed up for newsletters."
        });
    }).error(function (response){
        if(response.responseText == "input") {
            bootbox.alert({
                className: "delete-modal-font",
                message: "Inputed email is wrong."
            });
        } else {
            bootbox.alert({
                className: "delete-modal-font",
                message: "You already are registered for newsletters."
            });
        }
    });


});

