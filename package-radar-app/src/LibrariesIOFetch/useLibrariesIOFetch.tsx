import { useEffect, useState } from "react";

interface FetchErrorProp{
    setFetchError: React.Dispatch<React.SetStateAction<string|null|Error>>;
}
function useLibrariesIOFetch(query: string, platform: string, {setFetchError}:FetchErrorProp ){
    //MAKE SURE TO HIDE API KEY!!!!!!!!!!!!!!!!
    const BASE_LIBRARIES_URL = `https://libraries.io/api/${platform}/${query}?api_key=***REMOVED***`;
    const [num_dependents, setNum_dependents] = useState<number>();
    const [num_dependent_repos, setNum_dependent_repos] = useState<number>();
    useEffect(() => {

        const fecthLibrariesIOPackage = async () => {
            try{
                const response = await fetch('https://corsproxy.io?' + encodeURIComponent(BASE_LIBRARIES_URL));
                if(response.status >= 500){
                    throw new Error("fecthLibrariesIOPackage: Server error");
                }
                else if(response.status >= 400){
                    throw new Error("fecthLibrariesIOPackage: Client side error");
                }

                const packageInfo = await response.json();
                setNum_dependents(packageInfo.dependents_count);
                setNum_dependent_repos(packageInfo.dependent_repos_count)
            }
            catch(responseError: unknown){
                if(responseError instanceof Error){
                    setFetchError("fecthLibrariesIOPackage:" + responseError.message);
                }
                else{
                    setFetchError("Unkown error")
                }
            }
        }

        fecthLibrariesIOPackage();
    }, [query])

    return {num_dependents, num_dependent_repos};
}

export default useLibrariesIOFetch;