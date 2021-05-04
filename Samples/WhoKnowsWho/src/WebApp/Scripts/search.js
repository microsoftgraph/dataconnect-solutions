$(document).ready(function () {
    $('input.search-windows').keyup(function (event) {
        if (event.keyCode == 13) {
            this.form.submit();
            return false;
        }
    });
});