
$(document).ready(function(){
    callPromotion()

    //when submit register/edit form
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
            "minimumPrice": parseInt(serializedForm['minPrice']),
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
        console.log($('#registerModal').data("registermodalstate"));
        var url = ""
        var method = ""
        if($('#registerModal').data("registermodalstate")=="regis"){
            url = 'http://localhost:8089/promotions'
            method = "POST"
        }
        else if ($('#registerModal').data("registermodalstate")=="edit"){
            url = 'http://localhost:8089/promotions/edit/'+$('#registerModal').data("couponid")
            method = "PATCH"

        }
        console.log("Before")
        console.log(url, method)
        console.log(data)
        $.ajax({
            url: url,
            type: method,
            data: JSON.stringify(data),
            contentType: 'application/json'
         }).then(function(data){
            console.log("RESPONSE")
            //ถ้าสำเร็จ
            console.log(data)
            if(data['status'] == 'success'){
                
                $(".cardInstant").remove();
                callPromotion()
                console.log("OK")
                $('#registerModal').data("registerModalState", "regis")
                $('#registerModal').modal('hide')
            }
            else if(data['status'] === 'error'){
                console.log("error")
                $('#usedCodeError').css('display', 'inline-block');
            }
            
        })
        });

        $('.selectProduct').select2();

    $('#registerModal').on('hidden.bs.modal', function (e) {
        clearModal()
        $('#registerModal').data("registerModalState", "regis")
      })

    var dummy_data_product = [
        {
            'id':01,
            'name':'Tesla Model X',
            'description':'Electric car',
            'imageUrl':'url',
            'weight':999,
            'price':3099.99,
            'stock':100,
            'shopId':35,
            'categoryId':39,
        },
        {
            'id':02,
            'name':'Tesla Model Y',
            'description':'Electric car',
            'imageUrl':'url',
            'weight':999,
            'price':2099.99,
            'stock':100,
            'shopId':35,
            'categoryId':39,
        },
    ]

});


function swapPros(id, active, free){
    $.ajax({
        url: 'http://localhost:8089/promotions/edit/'+id,
        type: 'PATCH',
        data: JSON.stringify({'active':!active}),
        contentType: 'application/json'
     }).then(setTimeout (function(data){
        callPromotion(free)
     }, 500))
}

function deletePros(id){
    $.ajax({
        url: 'http://localhost:8089//promotions/delete/'+id,
        type: 'DELETE',
     }).then(setTimeout (function(data){
        setTimeout(callPromotion());
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
        var promotion = data['promotion']
        console.log($('#freeDeliCheck').val())
        $('#registerModal').modal('toggle')
        $('#freeDeliCheck').prop('checked', promotion['isFreeDelivery'])
        $('#type').get(0).selectedIndex = promotion['active']?0:1
        $('#discountCode').val(promotion["code"])
        $('#couponDesc').val(promotion['desc'])
        $('#minPrice').val(promotion['minimumPrice'])
        $('#discountBaht').val(promotion['discountInPrice']===0?"":promotion['discountInPrice'])
        $('#discountPercent').val(promotion['discountInPercent']===0?"":promotion['discountInPercent'])
        $('#startDate').val($.format.date(new Date(promotion['startDate']), "yyyy-MM-ddTH:mm:ss"))
        $('#dueDate').val($.format.date(new Date(promotion['dueDate']), "yyyy-MM-ddTH:mm:ss"))


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

        $('#registerModal').data("registermodalstate", "edit")
        $('#registerModal').data("couponid", id)

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
    $('#usedCodeError').css('display', 'none');
}




function callPromotion(free){
    $(".cardInstant").remove();
    var url = free?"http://localhost:8089/promotions/freeDelivery":"http://localhost:8089/promotions";
    $.ajax({
        url: url,
    }).then(function(data) {
        console.log(data);
        renderPromotion(data, free);
    });

}

function renderPromotion(data, free){
    $.each(data['promotion'], function(index, value){
        var card = "";
        card += "<div class=\"card col-lg-3 ml-5 mb-2 cardInstant\" style=\"width: 18rem;\"><div class=\"card-body\">"
        if (value.desc != null) {
            card += "<h5 class=\"card-text\">"+value.desc+"</h5>"
        }
        else{
            card += "<h5 class=\"card-text\">ไม่มีคำอธิบาย</h5>"
        }
        card += "<h5 class=\"card-text\">Code : "+value.code+"</h5>"
        card += "<h5 class=\"card-text\">Start : "+$.format.date(new Date(value.startDate), "d/MM/yyyy H:mm:ss")+"</h5>"
        card += "<h5 class=\"card-text\">End : "+$.format.date(new Date(value.dueDate), "d/MM/yyyy H:mm:ss")+"</h5>"
        card += "<h5 class=\"card-text\">Minimum Price : "+value.minimumPrice+" Baht</h5>"
        if (value.isFreeDelivery == true){
            card += "<h5 class=\"card-text text-primary\">Free delivery</h5>"
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
        card += "<button class=\"btn btn-primary mr-2\" onclick=\"swapPros("+value.id+", "+value.active+", "+free+")\">swap</button>"
        card += "<button class=\"btn btn-warning mr-2\" onclick=\"editPros("+value.id+", "+value.active+", "+free+")\">edit</button>"
        card += "<button class=\"btn btn-danger mr-2\" onclick=\"deletePros("+value.id+")\">Delete</button>"
        card += "</div>"
        
        $( ".cardCont" ).append(card);
    })
}


