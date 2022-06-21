function ShowToast() {
    console.log("showtoast");
    var myModalSuccess = document.getElementById('myModalSuccess')
    console.log("showtoast success", myModalSuccess);
    if (myModalSuccess != null) {
        var toast_success = new bootstrap.Toast(myModalSuccess);
        toast_success.show();
    }
}
function showImage(fileInput) {
    if (fileInput.files && fileInput.files[0]) {
        var reader = new FileReader();
        reader.onload = function (e) {
            $('#img').attr('src', e.target.result)
        }
        reader.readAsDataURL(fileInput.files[0]);
    }
}


