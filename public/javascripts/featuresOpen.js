/**
 * Created by User on 11/8/2015.
 */
$(document).ready(function(){
    $('#featuresOpen').click(function() {
        if ($('#featuresDiv').is(':hidden')) {
            $('#featuresDiv').show();
        } else {
            $('#featuresDiv').hide();
        }
    });
});