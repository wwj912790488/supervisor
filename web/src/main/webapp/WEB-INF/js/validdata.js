
// 正整数
function isInteger(str){
	var reg = /^[1-9]\d*$/;
	return reg.test(str);
}

// 负整数
function isInteger1(str){
	var reg = /^-[1-9]\d*$/;
	return reg.test(str);
}

// 非负整数
function isInteger2(str){
	var reg = /^([1-9]\d*|0)$/;
	return reg.test(str);
}

// 非正整数
function isInteger3(str){
	var reg = /^(-[1-9]\d*|0)$/;
	return reg.test(str);
}

//范围0-100整数
function isInteger4(str){
	var reg = /^([1-9]{1,2}|0|100)$/;
	return reg.test(str);
}

// 正浮点数
function isDecmal(str){
	var reg = /^([1-9]\d*.\d*[1-9]|0.\d*[1-9])$/;
	return reg.test(str);
}

// 负浮点数
function isDecmal1(str){
	var reg = /^(-[1-9]\d*.\d*[1-9]|-0.\d*[1-9])$/;
	return reg.test(str);
}

// 非负浮点数
function isDecmal2(str){
	var reg = /^([1-9]\d*.\d*[1-9]|0.\d*[1-9]|0)$/;
	return reg.test(str);
}

// 非正浮点数
function isDecmal3(str){
	var reg = /^(-[1-9]\d*.\d*[1-9]|-0.\d*[1-9]|0)$/;
	return reg.test(str);
}

//非负数字（包括整数和小数）
function isRequiredNumber(str){
	var reg = /^([1-9]\d*(\.\d+)?|0.\d*[1-9]|0)$/;
	return reg.test(str);
}

//范围0-100数字（包括整数和小数）
function isRequiredNumber1(str){
	var reg = /^(([1-9]{1,2})(\.\d+)?|100.0{1,}|0.\d*[1-9]|100|0)$/;
	return reg.test(str);
}

// 不全为空
function isSpace(str){
	var reg = /^\s*$/;
	return reg.test(str);
}

// 文件路径
function isFilePath(str){
	var reg = /^\/(.+\/)*(([^\.]*)$|.+\..+$)/;
	return reg.test(str);
}

// XML文件
function isXMLFile(str){
	str = str.toString().toUpperCase();
	var reg = /^(\s|\S)+\.XML$/;
	return reg.test(str);
}
