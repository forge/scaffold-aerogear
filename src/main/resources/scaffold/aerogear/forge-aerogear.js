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

			$.getJSON(aerogear.restUrl + '/../' + widget.dataset.rest, function(data) {
				for(var key in data) {
		        	select.append($('<option/>', { value : key, text: data[key] }));
				}
			});
		});
		
		aerogear.retrieve();		
	},
		
	// Create
	
	create: function() {
		aerogear.current = {};
		$('span.invalid').remove();
		$.mobile.changePage("#create-article");
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
            success: function(data) {
     	   
    			$.each(data, function(index,member) {
    				var row = "<tr onclick='aerogear.load(";
    				row += member.id;
    				row += ")' id='row1' class='member' style='cursor: hand'>";					
    				$('#search-results thead tr th').each(function(index,th) {
    					row += "<td>";
    					var value = member[th.id.substring('search-results-'.length)];
    					if ( value != null ) {
    						row += value;
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

    	aerogear.serializeForm('#search-form', aerogear.filter);
        aerogear.retrieve();
        return false;
    },

	load: function( id ) {
	    $.getJSON(aerogear.restUrl + '/' + id, function(data) {
	    	aerogear.current = data;
	    	aerogear.deserializeForm(data, '#view-fieldset');
	    	aerogear.deserializeForm(data, '#create-fieldset');
	    });
		$('span.invalid').remove();
	    $.mobile.changePage("#view-article");
		return false;
	},

    // Update

    update: function() {
        $('span.invalid').remove();
        aerogear.serializeForm('#create-form', aerogear.current);

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
     	   
            	aerogear.search = {};
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

		$.ajax({
			url: aerogear.restUrl + '/' + aerogear.current.id,
			type: 'DELETE',
			success: function() {

				aerogear.search = {};
				aerogear.retrieve();
				$.mobile.changePage("#search-article");
			}
    	});
	},

    serializeForm: function(src,dest) {
    	
        $( src + ' input,' + src + ' select' ).each(function() {
        	switch( this.type ) {
        	
    			case 'hidden':
    			case 'submit':
    				break;

        		case 'checkbox':
        			if ( this.checked ) {
        				dest[this.name] = 'true';
        			}
        			break;

        		default:
        			if ( this.value != '' ) {
        				dest[this.name] = this.value;
        			}
        	}        	
        });
    },

    deserializeForm: function(src,dest) {
    	
        $.each(src, function(key,value) {
        	$(dest + ' #' + key).each(function() {
        		if ( this.nodeName == 'DIV' ) {
        			this.innerHTML = value;
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
	        			this.value = value;
	        	}
        	});
        });
    },

};
