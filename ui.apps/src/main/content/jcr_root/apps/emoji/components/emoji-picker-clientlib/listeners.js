(function ($, $document) {
    "use strict";
    
    $document.on("dialog-ready", function() {
        $('#faceText').emojiInit();
    });

})($, $(document));
