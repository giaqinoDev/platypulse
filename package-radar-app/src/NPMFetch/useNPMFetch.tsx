import { useState, useEffect } from "react";

function useNPMFetch(queriedSearch: string|null){

    const BASE_URL ="https://registry.npmjs.org";
    const [queryResult, setQueryResult] = useState();
    const [loading, setLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>();

    useEffect(() => {
        if(!queriedSearch){
            return;
        }

        const executeFetch = async () => {
            setFetchError(null);
            setLoading(true);

            // Dev Lesson: Defining async functiosn inside useEffect() for data fetchign is common practice as defining it outside of useEffect would require the function
            // to be passed as a parameter in the useEffect hook which would then cause an infinite loop as the function is a new instance on each render
            try{
                //Using CORS Proxy Server
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
                    setFetchError(responseErorr);
                }
                else{
                    setFetchError("Unkown Error");
                }
            }
            finally{
                setLoading(false);
            }
        }

        executeFetch();
    }, [queriedSearch])

    return { queryResult, fetchError, loading };
}

export default useNPMFetch;