var aerogear = {

	restUrl: "rest/current",
	current: {},

	// Create
	
	create: function() {
		current = {};
		$('#create-form')[0].reset();
		$('span.invalid').remove();
		$.mobile.changePage("#create-article");
		return false;
	},
	
	// Retrieve
	
	retrieve: function() {
	    
		$.getJSON(aerogear.restUrl, function(data) {
			$('#pageItems tbody').empty();
			$.each(data, function(index,member) {
				var row = "<tr onclick='aerogear.load(";
				row += member.id;
				row += ")' id='row1' class='member' style='cursor: hand'>";					
				$('#pageItems thead tr th').each(function(index,th) {
					row += "<td>";
					row += member[th.id.substring('pageItems-'.length)];
					row += "</td>";
				});
				row += "</tr>";
				$('#pageItems tbody').append(row);
			});                    
		});
	},

	load: function( id ) {
	    $.getJSON(aerogear.restUrl + '/' + id, function(data) {
	        current = data;
	        $.each(current, function(key, val) {
	        	  $('#view-fieldset #' + key).html(val);
	        	  $('#create-fieldset #' + key).val(val);
				});
	    });
	    $('span.invalid').remove();
	    $.mobile.changePage("#view-article");
		return false;
	},

    // Update

    update: function() {
        $('span.invalid').remove();

        $.each($('#create-form').serializeArray(), function() {
            if (this.name!='save' && this.name!='delete') {
            	current[this.name] = this.value;
            }
        });

        var url = aerogear.restUrl;

		if ( current.id != null ) {
			url += '/' + current.id;
		}
        
        $.ajax({
            url: url,
            type: 'PUT',
            data: JSON.stringify(current),
            contentType: 'application/json',
            dataType: 'json',
            success: function(data) {
     	   
            	aerogear.retrieve();
     	   		$.mobile.changePage("#search-article");

            },
        	error: function(error) {
                 if (error.status == 412) {

                    $.each(JSON.parse(error.responseText), function(key, val){
                       $('<span class="invalid">' + val + '</span>')
                             .insertAfter($('#create-' + key));
                    });
                 } else {
                    $('#formMsgs').append($('<span class="invalid">Unknown server error</span>'));
                 }
        	}
        });
        
        return false;
    },

	// Delete

    remove: function() {
		$('span.invalid').remove();

		$.ajax({
			url: aerogear.restUrl + '/' + current.id,
			type: 'DELETE',
			success: function() {

				aerogear.retrieve();
				$.mobile.changePage("#search-article");
			}
    	});
	},
};
