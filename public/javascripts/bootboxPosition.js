/**
 * Created by boris.tomic on 15/10/15.
 */
$("body").on("shown.bs.modal", ".modal", function() {
    $(this).find('div.modal-dialog').css({
        'margin-top': function () {
            var modal_height = $('.modal-dialog').first().height();
            var window_height = $(window).height();
            return ((window_height/5) - (modal_height/5));
        }
    });
});