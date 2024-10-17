(function ($) {
    $('.navbar').fadeIn('slow').css('display', 'flex');
    // Summernote wysiwyg editor

    $("#editor").summernote({
        placeholder: 'enter directions here...',
        height: 300,
        callbacks: {
            onImageUpload: function (files, editor, welEditable) {

                for (var i = files.length - 1; i >= 0; i--) {
                    uploadFile(files[i], this);
                }
            },
            onMediaDelete: function(target) {
                alert(target[0].src);
                deleteFile(target[0].src);
            }
        }
          
    });

    // Summernote editor: send file to the server
    function uploadFile(file, el) {
        var form_data = new FormData();
        form_data.append('file', file);
        $.ajax({
            data: form_data,
            type: "POST",
            contentType: "multipart/form-data",
            url: '/image-upload',
            cache: false,
            contentType: false,
            processData: false,
            success: function (url) {
                //alert(url);

                $(el).summernote('editor.insertImage', url);
            }
        });
    }
    //
    // Summernote editor: delete file from the server.
    function deleteFile(src) {
        $.ajax({
            data: {src:src},
            type: "POST",
            contentType: "multipart/form-data",
            url: "/image-delete",
            cache: false,
            contentType: false,
            processData: false,
            success: function(resp) {
                console.log(resp);
            }
        });
    }
    




})(jQuery);

// Go to the next post
function redirectToNextPost(postId) {
    //var nextPostId = postId + 1;
    window.location.href = '/blog/posts/' + postId + '/next';
}

// Go to previous post
function redirectToPreviousPost(postId) {
    //var nextPostId = postId - 1;
    window.location.href = '/blog/posts/' + postId + '/previous';
}

// Open reply form
function openReplyForm(commentId) {
    window.location.href = '/blog/comments/' + commentId + '/reply';
}

// thumbnail preview before upload and update database
const thumbnailInput = document.getElementById('thumbnailInput');
const thumbnailPreview = document.getElementById('thumbnailPreview');
if(thumbnailInput) {
    thumbnailInput.addEventListener('change', () => {
    const reader = new FileReader();
    reader.onload = () => {
        thumbnailPreview.src = reader.result;
        thumbnailField.value = reader.result;
    };
    reader.readAsDataURL(thumbnailInput.files[0]);
});
}


// profile picture preview before upload and update database
const profilePictureInput = document.getElementById('profilePictureInput');
const profilePicturePreview = document.getElementById('profilePicturePreview');
const errorMsg = document.getElementById('errorMsg');

if(profilePictureInput) {
    profilePictureInput.addEventListener('change', () => {
        const reader2 = new FileReader();
        reader2.onload = () => {
            profilePicturePreview.src = reader2.result;
            errorMsg.remove();
        };
        reader2.readAsDataURL(profilePictureInput.files[0]);
    });
}


function checkPassword(input) {
    if (input.value != document.getElementById('password').value) {
      input.setCustomValidity('Passwords must match.');
    } else {
      // input is valid -- reset the error message
      input.setCustomValidity('')
    }
  }
  function checkEmail(input) {
    if (input.value != document.getElementById('email').value) {
      input.setCustomValidity('Emails must match.');
    } else {
      // input is valid -- reset the error message
      input.setCustomValidity('')
    }
  }

  window.onload = function() {
    var titleInput = document.getElementById('title');
    var slugInput = document.getElementById('slug');

    if (titleInput) {
        titleInput.addEventListener('input', function() {
        var titleValue = titleInput.value;
        var slugValue = titleValue.toLowerCase().replace(/[^a-zA-Z0-9 ]/g, '').replace(/\s/g, '-');
        slugInput.value = slugValue;
        });
    }

    
};
