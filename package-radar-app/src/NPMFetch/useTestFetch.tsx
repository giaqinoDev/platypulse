import { useEffect, useState } from "react";

function useTestFetch(){
    const [responseInfo, setResponseInfo] = useState<JSON|null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string|null|Error>(null)

    const [trigger, setTrigger] = useState<boolean>();

    useEffect(() => {
        console.log("Fetching from GH");
        if(!trigger){
            return undefined;
        }
        const fetchFromBackend = async () => {
            try{
                let response = null;
                response = await fetch('/bend/api/test/github-reg-test');
                const info = await response.json();
                setResponseInfo(info);
            }
            catch(e : unknown){
                if(e instanceof Error){
                    setError(e);
                }
                else{
                    setError("Unkown Error");
                }
            }
            setLoading(false);
        }
        fetchFromBackend();
    }, [trigger]);
    return ({responseInfo, loading, error, triggerFetch: () => {setTrigger(!trigger)}});
}

export default useTestFetch;