/**
 * Created by User on 9/13/2015.
 */
function deleteConfirmation(){
    var result = confirm("Do you want to delete?");
    if (result){
        return true;
    }
    else{
        return false;
    }
}

//$('body').on('click', 'a[data-role="delete"]', function(e){
//    e.preventDefault();
//    $toDelete = $(this);
//    var conf = bootbox.confirm("Delete?", function(result){
//        if(result != false){
//            $.ajax({
//                url: $toDelete.attr("href"),
//                method: "delete"
//            }).success(function(response){
//                $toDelete.parents($toDelete.attr("data-delete-parent")).remove();
//            });
//        }
//    });
//
//
//});