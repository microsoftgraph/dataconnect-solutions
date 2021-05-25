/*
 *  Copyright (c) Microsoft Corporation. All rights reserved. Licensed under the MIT license.
 *  See LICENSE in the project root for license information.
*/

$(document).ready(function () {
    $('input.search-windows').keyup(function (event) {
        if (event.keyCode == 13) {
            this.form.submit();
            return false;
        }
    });
});