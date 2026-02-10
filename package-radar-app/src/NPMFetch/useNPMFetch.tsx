import { useState, useEffect } from "react";

interface NPMFetchProps{
    queriedSearch: string;
}

function useNPMFetch({queriedSearch}:NPMFetchProps){

    const BASE_URL ="https://registry.npmjs.org";
    const [queryResult, setQueryResult] = useState();
    const [error, setError] = useState<string|null|unknown>();

    useEffect(() => {
        if(!queriedSearch){
            return;
        }

        const executeFetch = async () => {
            setError(null);
            try{
                const response = await fetch('https://corsproxy.io?' + encodeURIComponent(`${BASE_URL}/${queriedSearch}`));
                if(response.status >= 500){
                    throw new Error("Server error")
                }
                else if(response.status >= 400){
                    throw new Error("Client side error")
                }
                const packageInfo = await response.json();
                const latest_v = packageInfo["dist-tags"].latest;
                const latest_v_date = packageInfo.time[latest_v];
                setQueryResult(latest_v_date);
            }
            catch(responseErorr: unknown){
                if(responseErorr instanceof Error){
                    setError(responseErorr);
                }
                else{
                    setError("Unkown Error");
                }
            }
        }

        executeFetch();
    }, [queriedSearch])

    return { queryResult, error };
}

export default useNPMFetch;