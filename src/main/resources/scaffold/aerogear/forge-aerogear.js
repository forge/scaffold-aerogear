var aerogear = {

	restUrl: "rest/current",
	current: {},
	filter: {},

	// Initialize
	
	initialize: function(restUrl) {
		
		aerogear.restUrl = restUrl;
		
		$('#create-form select, #search-form select').each(function(key,widget) {
        	
			var select = $(widget);
			select.empty();
			select.append($('<option/>'));

			$.getJSON(aerogear.restUrl + '/../' + widget.dataset.rest, function(list) {
				$.each(list, function(index,data) {
					select.append($('<option/>', { value : data.id, text: data.toString }));
				});
			});
		});
		
		aerogear.retrieve();		
	},
		
	// Create
	
	create: function() {
		$.mobile.changePage("#create-article");
		aerogear.current = {};
		$('#create-form')[0].reset();
		$('#create-form input[type="checkbox"]').attr('checked',false).checkboxradio('refresh');
		$('#create-form select').val(null).selectmenu('refresh');		
		$('span.invalid').remove();
		return false;
	},
	
	// Retrieve
	
	retrieve: function() {
	    
		$('#search-results tbody').empty();

		$.ajax({
            url: aerogear.restUrl + '/search',
            type: 'POST',
            data: JSON.stringify(aerogear.filter),
            contentType: 'application/json',
            dataType: 'json',
            success: function(list) {
    			$.each(list, function(index,data) {
    				var row = "<tr onclick='aerogear.load(";
    				row += data.id;
    				row += ")'>";					
    				$('#search-results thead tr th').each(function(tr,th) {
    					row += "<td>";
    					var key = th.id.substring('search-results-'.length);
    					var value = data[key];
    					if ( value != null ) {
	    					if ( typeof value == 'object' ) {
	    						row += value.toString;
	    					} else {
	   							row += value.toString();
	    					}
    					}
    					row += "</td>";
    				});
    				row += "</tr>";
    				$('#search-results tbody').append(row);
    			});
            }
        });
	},

    search: function() {

    	aerogear.serializeFromForm('#search-form', aerogear.filter);
        aerogear.retrieve();
        return false;
    },

	load: function( id ) {
	    $.getJSON(aerogear.restUrl + '/' + id, function(data) {
	    	aerogear.current = data;
	    	aerogear.serializeToForm(data, '#view-fieldset');
	    	aerogear.serializeToForm(data, '#create-fieldset');
	    });
		$('span.invalid').remove();
	    $.mobile.changePage("#view-article");
		return false;
	},

    // Update

    update: function() {
        $('span.invalid').remove();
        aerogear.serializeFromForm('#create-form', aerogear.current);

        var url = aerogear.restUrl;

		if ( aerogear.current.id != null ) {
			url += '/' + aerogear.current.id;
		}
        
        $.ajax({
            url: url,
            type: 'PUT',
            data: JSON.stringify(aerogear.current),
            contentType: 'application/json',
            dataType: 'json',
            success: function(data) {
     	   
            	aerogear.filter = {};
            	aerogear.retrieve();
     	   		$.mobile.changePage("#search-article");

            },
        	error: function(error) {
                 if (error.status == 412) {

                    $.each(JSON.parse(error.responseText), function(key, val){
                       $('<span class="invalid">' + val + '</span>')
                             .insertAfter($('#create-fieldset #' + key));
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

		if ( !confirm( 'OK to delete?' )) {
			return;
		}
		
		$.ajax({
			url: aerogear.restUrl + '/' + aerogear.current.id,
			type: 'DELETE',
			success: function() {

				aerogear.filter = {};
				aerogear.retrieve();
				$.mobile.changePage("#search-article");
			},
	    	error: function(error) {
               $('#formMsgs').append($('<span class="invalid">Unknown server error</span>'));
	    	}
    	});
	},

	// Internal functions
	
    serializeFromForm: function(src,dest) {
    	
        $( src + ' input,' + src + ' select' ).each(function() {
        	switch( this.type ) {
        	
    			case 'hidden':
    			case 'submit':
    				break;

        		case 'checkbox':
        			if ( this.checked ) {
        				dest[this.name] = 'true';
        			} else {
        				dest[this.name] = 'false';
        			}
        			break;

        		default:
        			if ( this.value == '' ) {
        				dest[this.name] = null;
        			} else {
    					if ( typeof this.value == 'object' ) {
            				dest[this.name] = this.value.toString;
    					} else {
            				dest[this.name] = this.value;
    					}
        			}
        	}        	
        });
    },

    serializeToForm: function(src,dest) {
    	
        $.each(src, function(key,value) {
        	$(dest + ' #' + key).each(function() {

        		if ( this.nodeName == 'SELECT' ) {
        			if (value == null) {
        				value = '';
        			} else {
        				value = value.id;
        			}
        			$(dest + ' #' + key).val(value).selectmenu('refresh');
        			return;
        		}
        		
    			switch( this.type ) {
	        	
	    			case 'hidden':
	    			case 'submit':
	    				break;
	
	        		case 'checkbox':
	        			$(dest + ' #' + key).attr('checked', value).checkboxradio('refresh');
	        			break;
	
	        		default:
    					if ( value != null && typeof value == 'object' ) {
    						this.value = value.toString;
    					} else {
    						this.value = value;
    					}
	        	}
        	});
        });
    },
};
