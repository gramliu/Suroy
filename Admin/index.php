<?php

require __DIR__.'/vendor/autoload.php';

use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;

$serviceAccount = ServiceAccount::fromJsonFile(__DIR__.'/hack4ph-suroy-11cb3-firebase-adminsdk-7j38i-217e20d70b.json');
$firebase = (new Factory)
    ->withServiceAccount($serviceAccount)
    // The following line is optional if the project id in your credentials file
    // is identical to the subdomain of your Firebase project. If you need it,
    // make sure to replace the URL with the URL of your project.
    ->withDatabaseUri('https://hack4ph-suroy-11cb3.firebaseio.com')
    ->create();
$db = $firebase->getDatabase();
$ref = $db->getReference('user-test/drivers');
$count = 0;
while(true){
    $count++;

    $curl = curl_init();

    curl_setopt_array($curl, array(
    CURLOPT_URL => "https://forms.hack4ph.gov.ph/fr/service/persistence/search/dyipers/Suroy_Driver",
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_ENCODING => "",
    CURLOPT_MAXREDIRS => 10,
    CURLOPT_TIMEOUT => 30,
    CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
    CURLOPT_CUSTOMREQUEST => "POST",
    CURLOPT_POSTFIELDS => "<search>\n    <drafts>include</drafts>\n    <page-size>1</page-size>\n    <page-number>$count</page-number>\n    <lang>en</lang>\n</search>\n",
    CURLOPT_HTTPHEADER => array(
        "Content-Type: application/xml",
        'Authorization: Basic '. base64_encode("dyipers:me3K8cn5jmc6Zsmh")
    ),
    ));

    $response = curl_exec($curl);
    $err = curl_error($curl);

    curl_close($curl);

    
    $data = simplexml_load_string($response);
    $jso = json_encode($data);
    $arr = json_decode($jso, TRUE);
    if(array_key_exists("document", $arr)){
        $name = $arr["document"]["@attributes"]["name"];
        $ch = curl_init();

        curl_setopt_array($ch, array(
        CURLOPT_URL => "https://forms.hack4ph.gov.ph/fr/service/persistence/crud/dyipers/Suroy_Driver/data/$name/data.xml",
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_ENCODING => "",
        CURLOPT_MAXREDIRS => 10,
        CURLOPT_TIMEOUT => 30,
        CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
        CURLOPT_CUSTOMREQUEST => "GET",
        CURLOPT_HTTPHEADER => array(
            "Content-Type: application/xml",
            'Authorization: Basic '. base64_encode("dyipers:me3K8cn5jmc6Zsmh")
        ),
        ));

        $response = curl_exec($ch);
        $err = curl_error($ch);
        $entry = simplexml_load_string($response);
        $json = json_encode($entry);
        $arra = json_decode($json, TRUE);

        curl_close($ch);

        if ($err) {
        echo "cURL Error #:" . $err;
        } else {
            // print_r($arra["section-1"]["control-2"]);
            
            $postData = [
                'first_name' => $arra["section-1"]["control-1"],
                'last_name' => $arra["section-1"]["control-2"],
                'route_id' => (int) $arra["section-1"]["control-4"]
            ];
            $ref->getChild(md5($arra["section-1"]["control-3"]))->set($postData);
        }
        break;
    }else{
        break;
    }
    
}

?>