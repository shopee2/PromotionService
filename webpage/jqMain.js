
$(document).ready(function(){
    callAllPromotion()
    $("#regisForm").submit(function(e){
        e.preventDefault();
        //console.log("nah")
        var serializedForm = $( "#regisForm" ).serializeJSON()
        //console.log(serializedForm)
        var disBaht = (serializedForm['discountBaht'] === undefined || serializedForm['discountBaht'] === "")?0:parseFloat(serializedForm['discountBaht'])
        var disPer = (serializedForm['discountPercent'] === undefined || serializedForm['discountPercent'] === "")?0:parseFloat(serializedForm['discountPercent'])
        var data =  {
            //3อันแรก เป็นDefault
            "productNo": "-1",
            "forShopID": "-1",
            "creatorID": "-1",

            "active": serializedForm['type']=="active",
            "code": serializedForm['discountCode'],
            "startDate": new Date(serializedForm['startDate']).toISOString(),
            "dueDate": new Date(serializedForm['dueDate']).toISOString(),
            "minimumPrice": serializedForm['minPrice'],
            "discountInPrice": disBaht,
            "discountInPercent": disPer,
            "isFreeDelivery": $("#freeDeliCheck")[0].checked,
            "desc": serializedForm['couponDesc']
        }
        //console.log(data)
        /*
            couponDesc: ""
            couponName: ""
            discountBaht: ""
            discountCode: ""
            discountPercent: ""
            dueDate: ""
            minPrice: ""
            startDate: ""
            type: "active"
        */
        console.log($('#registerModal').data("registerModalState"));
        var url = ""
        var method = ""
        if($('#registerModal').data("registerModalState")==="regis"){
            url = 'http://localhost:8089/promotions'
            method = "POST"
        }
        else if ($('#registerModal').data("registerModalState")==="edit"){
            url = 'http://localhost:8089/promotions?id='+$('#registerModal').data("couponId")
            method = "PATCH"

        }
        $.ajax({
            url: url,
            type: method,
            data: JSON.stringify(data),
            contentType: 'application/json'
         }).then((setTimeout (function(data){

            //ถ้าสำเร็จ
            $(".cardInstant").remove();
            setTimeout(callAllPromotion(), 100000);
            console.log(data)
            $('#registerModal').data("registerModalState", "regis")
            $('#registerModal').modal('hide')
        }, 500)))
        });

    $('#registerModal').on('hidden.bs.modal', function (e) {
        clearModal()
      })

});

function swapPros(id, active){
    $.ajax({
        url: 'http://localhost:8089/promotions?id='+id,
        type: 'PATCH',
        data: JSON.stringify({'active':!active}),
        contentType: 'application/json'
     }).then(setTimeout (function(data){
        $(".cardInstant").remove();
        setTimeout(callAllPromotion());
     }, 500))
}

function deletePros(id){
    $.ajax({
        url: 'http://localhost:8089/promotions?id='+id,
        type: 'DELETE',
     }).then(setTimeout (function(data){
        setTimeout(callAllPromotion());
        $(".cardInstant").remove();
     }, 500))
}

function freeDeli(checkbox){
    if(checkbox.checked == true){
        $( "#discountBaht" ).prop( "disabled", true );
        $( "#discountBaht" ).val("")
        $( "#discountPercent" ).prop( "disabled", true );
        $( "#discountPercent" ).val("")
    }
    else{
        $( "#discountBaht" ).prop( "disabled", false );
        $( "#discountPercent" ).prop( "disabled", false );
    }
}

function editPros(id, active){
    
    $.ajax({
        url: 'http://localhost:8089/promotions/id/'+id,
        type: 'GET',
     }).then(function(data){
        console.log($('#freeDeliCheck').val())
        $('#registerModal').modal('toggle')
        $('#freeDeliCheck').prop('checked', data['isFreeDelivery'])
        $('#type').get(0).selectedIndex = data['active']?0:1
        $('#discountCode').val(data["code"])
        $('#couponDesc').val(data['desc'])
        $('#minPrice').val(data['minimumPrice'])
        $('#discountBaht').val(data['discountInPrice']===0?"":data['discountInPrice'])
        $('#discountPercent').val(data['discountInPercent']===0?"":data['discountInPercent'])
        $('#startDate').val($.format.date(new Date(data['startDate']), "yyyy-MM-ddTH:mm:ss"))
        $('#dueDate').val($.format.date(new Date(data['dueDate']), "yyyy-MM-ddTH:mm:ss"))


        if($('#freeDeliCheck').prop('checked') == true){
            $( "#discountBaht" ).prop( "disabled", true );
            $( "#discountBaht" ).val("")
            $( "#discountPercent" ).prop( "disabled", true );
            $( "#discountPercent" ).val("")
        }
        else{
            $( "#discountBaht" ).prop( "disabled", false );
            $( "#discountPercent" ).prop( "disabled", false );
        }

        $('#registerModal').data("registerModalState", "edit")
        $('#registerModal').data("couponId", id)

     })
     /*
        active: false
        code: "THEBESTCODE"
        creatorID: "-1"
        desc: "best"
        discountInPercent: 0
        discountInPrice: 0
        dueDate: "2020-11-15T07:54:00.000+00:00"
        forShopID: "-1"
        id: 11
        isFreeDelivery: true
        minimumPrice: 700
        productNo: "-1"
        startDate: "2020-11-15T14:49:00.000+00:00"
     */
}
function clearModal(){
    $('#freeDeliCheck').prop('checked', false)
    $('#type').get(0).selectedIndex = 0
    $('#discountCode').val("")
    $('#couponDesc').val("")
    $('#minPrice').val("")
    $('#discountBaht').val("")
    $('#discountPercent').val("")
    $('#startDate').val("")
    $('#dueDate').val("")
}




function callAllPromotion(free){
    $(".cardInstant").remove();
    var url = free?"http://localhost:8089/promotions/freeDelivery":"http://localhost:8089/promotions";
    $.ajax({
        url: url,
    }).then(function(data) {
        console.log(data);
        renderPromotion(data);
    });

}

function renderPromotion(data){
    $.each(data, function(index, value){
        var card = "";
        card += "<div class=\"card col-lg-3 ml-5 mb-2 cardInstant\" style=\"width: 18rem;\"><div class=\"card-body\">"
        if (value.desc != null) {
            card += "<h5 class=\"card-text\">"+value.desc+"</h5>"
        }
        else{
            card += "<h5 class=\"card-text\">ไม่มีคำอธิบาย</h5>"
        }
        card += "<h5 class=\"card-text\">code : "+value.code+"</h5>"
        card += "<h5 class=\"card-text\">start : "+$.format.date(new Date(value.startDate), "d/MM/yyyy H:mm:ss")+"</h5>"
        card += "<h5 class=\"card-text\">end : "+$.format.date(new Date(value.dueDate), "d/MM/yyyy H:mm:ss")+"</h5>"
        card += "<h5 class=\"card-text\">Minimum Price : "+value.minimumPrice+" Baht</h5>"
        if (value.isFreeDelivery == true){
            card += "<h5 class=\"card-text\">Free delivery</h5>"
        }
        else{
            
            if(value.discountInPercent == 0){
                card += "<h5 class=\"card-text\">Discount : "+value.discountInPrice+" Baht</h5>"
            }
            if(value.discountInPrice == 0){
                card += "<h5 class=\"card-text\">Discount : "+value.discountInPercent+"%</h5>"
            }
        }
        card += (value.active)?"<h5 class=\"card-text\">Status : <span class='text-success'>Active</span></h5>":"<h5 class=\"card-text\">Status : <span class='text-danger'>Inactive</span></h5>";
        card += "<button class=\"btn btn-primary\" onclick=\"swapPros("+value.id+", "+value.active+")\">swap</button>"
        card += "<button class=\"btn btn-warning\" onclick=\"editPros("+value.id+", "+value.active+")\">edit</button>"
        card += "<button class=\"btn btn-danger\" onclick=\"deletePros("+value.id+")\">Delete</button>"
        card += "</div>"
        
        $( ".cardCont" ).append(card);
    })
}


