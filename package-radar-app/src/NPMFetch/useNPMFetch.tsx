import { useState, useEffect } from "react";

function useNPMFetch(queriedSearch: string|null){

    const BASE_PACKAGE_URL ="https://registry.npmjs.org";
    const BASE_DOWNLOAD_URL = "https://api.npmjs.org/downloads/point";
    const [queryResult, setQueryResult] = useState(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>();
    const [packageData, setPackageData] = useState<NPMPackage_Data_Primative|null>(null);

    interface NPMPackage_Data_Primative{
        //Package Releases:
        latest_v: string,
        releases: object,

        //Dependency Structure:
        dir_dependency_count: number,
        //dir_dependents_count: number,



    }

    useEffect(() => {
        if(!queriedSearch){
            return;
        }

        const fetchPackageData = async () => {
            setFetchError(null);
            setLoading(true);
            setQueryResult(null);

            // Dev Lesson: Defining async functiosn inside useEffect() for data fetching is common practice as defining it outside of useEffect would require the function
            // to be passed as a parameter in the useEffect hook which would then cause an infinite loop as the function is a new instance on each render
            try{
                //Using CORS Proxy Server
                const response = await fetch('https://corsproxy.io?' + encodeURIComponent(`${BASE_PACKAGE_URL}/${queriedSearch}`));
                if(response.status >= 500){
                    throw new Error("Server error")
                }
                else if(response.status >= 400){
                    throw new Error("Client side error")
                }
                const packageInfo = await response.json();
                const latest_release = packageInfo["dist-tags"].latest;
                const queriedPackageData: NPMPackage_Data_Primative = {
                    latest_v: latest_release,
                    releases: packageInfo.time,

                    dir_dependency_count: Object.keys(packageInfo.versions[`${latest_release}`].dependencies).length,
                    //dir_dependents_count: packageInfo.versions.latest_v.
                }
                
                setPackageData(queriedPackageData);
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

        const fetchDownloadData = async () => {
            //const response = await fetch('https://corsproxy.io?' + encodeURIComponent(`${BASE_DOWNLOAD_URL}/`))
        }

        fetchPackageData();
    }, [queriedSearch])

    return { packageData, fetchError, loading };
}

export default useNPMFetch;