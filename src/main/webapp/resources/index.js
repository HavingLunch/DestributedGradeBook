/**
 * 
 */

    $(document).ready(function() {
        let primaryUrl = $(location)[0].href.toString().replace("/index.html", "");
        $("#primary").val(primaryUrl);
        $("#secondary").val("");
        
        function getSecondaryUrl(){
	        $.get(primaryUrl.concat('ServerConfiguration/SecondaryServer'),
	            function(data) {
	                if (data){
	                    let secondaryUrlNodes = data.getElementsByTagName("url");
	                    let secondaryUrl = "";
	                    if(secondaryUrlNodes.length > 0)  secondaryUrl = secondaryUrlNodes[0].textContent;
	                    if(secondaryUrl !== ""){
	                        $("#statusSecondary").text("");
	                        $("#secondary").val(secondaryUrl);}
	                }
	
	                if($("#secondary").val() === ""){
	                    $("#statusSecondary").text("The Secondary Server hasn't been set up!");
	                }
	            });
        }
        
        getSecondaryUrl();
        
        $("#submmit").click(function(){
            if($("#primary").val()==="" || !isUrlValid($("#primary").val())) {
            	$("#statusPrimary").text("Invalid URL!");
            	return;
            }
            if($("#secondary").val()==="" || !isUrlValid($("#secondary").val())) {
            	$("#statusSecondary").text("Invalid URL!");
            	return;
            }
            let postPrimaryXml = "<server><type>primary</type><url>" + $("#primary").val() + "</url></server>";
            let postSecondaryXml = "<server><type>secondary</type><url>" + $("#secondary").val() + "</url></server>";
            
            let restfulURLPrimary = $("#primary").val().concat("ServerConfiguration/PrimaryServer");
            let restfulURLSecondary = $("#primary").val().concat("ServerConfiguration/SecondaryServer");
            $.ajax({ 
            	type: "POST",
                url: restfulURLPrimary,
                dataType: "xml",
                data: postPrimaryXml,
                contentType: "application/xml",                
                cache: false,
                error: function() { $("#statusPrimary").text("Primary Server setting failed!"); },
                success: function() {
                    $("#statusPrimary").text("Primary Server was updated!");
                }
            });
            $.ajax({ 
            	type: "POST",
                url: restfulURLSecondary,
                dataType: "xml",
                data: postSecondaryXml,
                contentType: "application/xml",
                cache: false,
                error: function() { $("#statusSecondary").text("Secondary Server setting failed!"); },
                success: function() {
                    $("#statusSecondary").text("Secondary Server was updated!");
                }
            });

        });
    });
    
    function isUrlValid(userInput) {
    	let primaryUrl = $(location)[0].href.toString().replace("/index.html", "");
    	let result;
    	console.log(primaryUrl.concat("ServerConfiguration/URLVerification"))
    	$.ajax({
    		async: false,
    		type: 'POST',
    		url: primaryUrl.concat("ServerConfiguration/URLVerification"),
    		dataType: "text",    
    		contentType: "text/plain",
    		data: encodeURI(userInput),
    		success: function(data) {
    	    	 result=data; 
    	     },
	    	error: function (xhr, ajaxOptions, thrownError) {
	    		console.log(xhr.status);
	    		console.log(thrownError);}
    	});
    	console.log(result);
        return result==="true" ? true:false;
    }
