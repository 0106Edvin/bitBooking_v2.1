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
        swal({
            title: 'You successfully signed up for newsletters.',
            text: 'Window will close in 3 seconds.',
            type: 'success',
            timer: 3000
        });
    }).error(function (response){
        if(response.responseText == "input") {
            swal({
                title: 'Inputted email is wrong.',
                text: 'Window will close in 2 seconds.',
                type: 'error',
                timer: 2000
            });
        } else {
            swal({
                title: 'You already are registered for newsletters.',
                text: 'Window will close in 2 seconds.',
                type: 'error',
                timer: 2000
            });
        }
    });
});

