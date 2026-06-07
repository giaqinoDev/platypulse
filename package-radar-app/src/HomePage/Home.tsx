import { useEffect, useLayoutEffect, useRef, useState } from "react";
import "@fontsource/geist-sans/400.css";
import "@fontsource/geist-sans/200.css";

import "./Home.css";
import useNPMFetch from "../NPMFetch/useNPMFetch";
import SearchBar from "../util-components/SearchBarComponents/SearchBar";
import type { SearchContextStructure } from "../contexts/RegistrySearch/SearchContextStructure";
import SplashGraphgic from "../util-components/HomePageSplashGraphic/SplashGraphic";
import DetailsGrid from "../util-components/HomePageDetailsGrid/DetailsGrid";
import MenuHeader from "../util-components/MenuHeader/MenuHeader";

function Home(){

    const [query, setQuery] = useState<SearchContextStructure|null>(null);
    const exResult = useNPMFetch(query);
    const homePageHeroRef = useRef<HTMLDivElement>(null);
    
    return(
        <div>
            <MenuHeader/>
            <div className="home-page-header" ref={homePageHeroRef}>
                <div className="center-header">
                    <div className="home-page-hero-section">
                        <h1 className="title">Make dependency decisions backed by <span className="highlight-word">signals</span>, not guesswork.</h1>
                        <p className="header">
                            Monitor the health of packages across the NPM ecosystem and retrieve up to date reporting to guide decisions on package inclusion.
                        </p>
                        
                        <SearchBar onSearch={setQuery}/>
                    </div>
                    
                    <SplashGraphgic/>
                </div>
            </div>
            <div style={{"display": "flex", "justifyContent": "center", "padding": "40px 0px"}}>
                <DetailsGrid/>
            </div>
            {exResult.loading && 
                <div>Loading</div>
            }

            {exResult.fetchError && 
                <div id="info">
                    <p className="info">{exResult.fetchError.toString()}</p>
                </div>
            }
            {exResult.packageData && 
                <div id="info">
                    {Object.entries(exResult.packageData).map(([key, value]) => (
                        <div key={key}>
                            <p className="info">{`${key}: ${String(value)}`}</p>
                        </div>
                    ))}
                </div>
            }
            
            
        </div>
    );
}

export default Home;