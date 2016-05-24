/**
 * 存放主要交互逻辑代码
 * 模块化
 * Created by kangb on 2016/5/24.
 */

var seckill = {
    //封装秒杀相关ajax的url
    URL : {
        now : function(){
            return '/seckill/time/now';
        },
        exposer : function(seckillId){
            return '/seckill/'+seckillId+'/exposer';
        },
        execution : function(seckillId,md5){
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;//直接判断对象会看对象是否为空,空就是undefine就是false; isNaN 非数字返回true
        } else {
            return false;
        }
    },
    countDown :function (seckillId, nowTime, startTime, endTime){
        var seckillBox = $('#seckill-box');
        //时间判断
        if (nowTime > endTime){
            //秒杀结束
            seckillBox.html('秒杀结束');

        }else if(nowTime <startTime){
            //秒杀未开始，计时时间绑定
            var killTime = new Date(startTime +1000);
            console.log("fff"+killTime);
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒 ');
                seckillBox.html(format);
                //时间完成以后回调
            }).on('finish.countdown',function(){
                //获取秒杀地址，控制显示逻辑
                seckill.handleSeckill(seckillId,seckillBox);
            })
        }else {
            //秒杀开始
            seckill.handleSeckill(seckillId,seckillBox);
            console.log("秒杀开始"+killTime);
        }
    },

    detail : {
        //详情页初始化
        init : function(params){
            //手机验证和登录，几时交互
            //规划我们的交互流程
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');

            if (!seckill.validatePhone(killPhone)){
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true , //显示弹出层
                    backdrop: 'static' , //禁止位置关闭
                    keyboard: false //关闭键盘事件
                });

                $('#killPhoneBtn').click(function(){
                    var inputPhone = $('#killPhoneKey').val();
                    console.log("inputhone={}",inputPhone);//TODO
                    if (seckill.validatePhone(inputPhone)){
                        $.cookie('killPhone',inputPhone,{expires:7,path: '/seckill'});
                        window.location.reload();

                    }else {

                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！ </label>').show(300);


                    }
                });
            }
            //已经登录

            //计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    //时间判断 计时交互
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result: ' + result);
                    alert('result: ' + result);
                }
            });


        }
    },

    handleSeckill : function(seckillId,node){

        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀哦</button>');
        //处理秒杀逻辑

        $.post(seckill.URL.exposer(seckillId),{},function(result){
            //在回调函数中执行交互逻辑
            if(result&&result['success']){
                var exposer = result['data'];
                if (exposer['exposed']){
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5'] ;
                    var killUrl = seckill.URL.execution(seckillId,md5) ;
                    console.log("killUrl"+killUrl);
                    //绑定一次点击事件
                    $('#killBtn').one('click',function(){
                        //绑定执行请求
                        $(this).addClass('disabled');
                        //发送秒杀请求
                        $.post(killUrl,{},function(result){
                            if(result&&result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+' </span>');
                            }

                        });

                    });


                    node.show();
                }else {
                    //未开启
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end  = exposer['end'] ;
                    seckill.countDown(seckillId,now,start,end);
                }
            }else {
                console.log("result"+result);
            }
        });

    }


}