Reported by tiny051401
```
class NplRequest{
	private $fudanUrl = "http://jkx.fudan.edu.cn/fudannlp/";
	private $connecttimeout = 20;
	private $timeout = 10;
	private $ssl_verifypeer = FALSE;
	
	public $http_code;
	public $http_info = array();
	public $url;
	
	function npl($key,$str){
		$response = $this->http($this->fudanUrl.$key."/".$str,"GET","");
		return $response;
	}
	
	function http($url,$method,$param){
		$ci = curl_init();
		curl_setopt($ci, CURLOPT_HTTP_VERSION, CURL_HTTP_VERSION_1_0);
		curl_setopt($ci, CURLOPT_CONNECTTIMEOUT, $this->connecttimeout);
		curl_setopt($ci, CURLOPT_TIMEOUT, $this->timeout);
		curl_setopt($ci, CURLOPT_RETURNTRANSFER, TRUE);
		curl_setopt($ci, CURLOPT_ENCODING, "");
		curl_setopt($ci, CURLOPT_SSL_VERIFYPEER, $this->ssl_verifypeer);
		curl_setopt($ci, CURLOPT_SSL_VERIFYHOST, 1);
		curl_setopt($ci, CURLOPT_HEADER, FALSE);
		
		if($method == "POST"){
			curl_setopt($ci, CURLOPT_POST, TRUE);
		}else{
			$url = "{$url}?{$param}";
		}
		curl_setopt($ci, CURLOPT_URL, $url );
		curl_setopt($ci, CURLINFO_HEADER_OUT, TRUE );
		
		$response = curl_exec($ci);
		$this->http_code = curl_getinfo($ci, CURLINFO_HTTP_CODE);
		$this->http_info = array_merge($this->http_info, curl_getinfo($ci));
		$this->url = $url;
		curl_close ($ci);
		return $response;
	}
}
```