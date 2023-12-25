function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

  function sendMsgApi(data){
    return $axios({
        'url':'/user/sendMsg',
        'method':'post',
        data
    }).then(response => {
        // 请求成功时打印日志
        alert('请求成功:', response);
        return response; // 返回响应数据，以便后续处理
    }).catch(error => {
        // 在这里处理错误
        console.error('请求失败:', error);
        throw error; // 抛出错误以便后续处理
        });

  }

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

  