/**
 * Created by User on 11/8/2015.
 */
$(document).ready(function(){
    $('#sort').DataTable({
        "aLengthMenu": [[25, 50, 75, -1], [25, 50, 75, "All"]],
        "iDisplayLength": 25
    });
});